package com.aethersight.companion.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

@SuppressLint("MissingPermission")
class BleManager(private val context: Context) {

    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var bluetoothGatt: BluetoothGatt? = null

    private val clientConfigDescriptorUuid: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private val _connectionState = MutableStateFlow("Disconnected")
    val connectionState: StateFlow<String> = _connectionState

    private val _deviceData = MutableStateFlow("Waiting for data...")
    val deviceData: StateFlow<String> = _deviceData

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            if (device.name?.contains("Blaze S", ignoreCase = true) == true) {
                Log.d("BleManager", "Found Carbinox Blaze S: ${device.name} - ${device.address}")
                stopScan()
                connectToDevice(device)
            }
        }
    }

    fun startScan() {
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        if (scanner == null) {
            _connectionState.value = "BLE Scanner unavailable"
            return
        }
        _connectionState.value = "Scanning for Blaze S..."
        scanner.startScan(scanCallback)
    }

    fun stopScan() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
    }

    private fun connectToDevice(device: BluetoothDevice) {
        _connectionState.value = "Connecting to ${device.name}..."
        bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _connectionState.value = "Connected to Blaze S"
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = "Disconnected"
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("BleManager", "GATT Services discovered.")
                enableGattNotifications(gatt)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            val payload = value.joinToString(" ") { String.format("%02X", it) }
            val asciiString = value.decodeToString().filter { it in ' '..'~' }
            _deviceData.value = "HEX: $payload\nTEXT: $asciiString"
        }
    }

    private fun enableGattNotifications(gatt: BluetoothGatt) {
        for (service in gatt.services) {
            for (characteristic in service.characteristics) {
                val props = characteristic.properties
                if ((props and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0 ||
                    (props and BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {

                    gatt.setCharacteristicNotification(characteristic, true)
                    val descriptor = characteristic.getDescriptor(clientConfigDescriptorUuid)
                    if (descriptor != null) {
                        descriptor.value = if ((props and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        } else {
                            BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                        }
                        gatt.writeDescriptor(descriptor)
                        Log.i("BleManager", "Subscribed to GATT Stream: ${characteristic.uuid}")
                    }
                }
            }
        }
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        _connectionState.value = "Disconnected"
    }
}
