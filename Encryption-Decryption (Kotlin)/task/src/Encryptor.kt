package encryptdecrypt

import java.io.File
import java.io.FileNotFoundException
import kotlin.system.exitProcess

class Encryptor {
    var mode: Mode = Mode.ENC
    private var key: Int = 0
    private var data: String = String()
    private var srcFileName: String? = null
    private var desFileName: String? = null
    private var alg: EncryptionAlgorithm = EncryptionAlgorithm.SHIFT
    private lateinit var input: String
    private lateinit var output: String

    fun parseArguments(args: Array<String>) {
        for (i in args.indices) {
            val a = args[i]
            when (a) {
                "-mode" -> setMode(args[i+1])
                "-key" ->  this.key = getKey(args[i+1])
                "-data" -> this.data = args[i+1]
                "-in" -> this.srcFileName = args[i+1]
                "-out" -> this.desFileName = args[i+1]
                "-alg" -> setAlg(args[i+1])
                else -> continue
            }
        }
    }

    private fun setMode(mode: String) {
        when (mode.lowercase()) {
            "enc" ->  this.mode = Mode.ENC
            "dec" -> this.mode = Mode.DEC
            else -> {
                println("Error: Invalid mode, must be enc or dec")
                exitProcess(-1)
            }
        }
    }

    private fun getKey(key: String): Int {
        return try {
            key.toInt()
        } catch (e: NumberFormatException) {
            println("Error: Invalid key, must be an integer")
            exitProcess(-1)
        }
    }

    private fun setAlg(alg: String) {
        when (alg.lowercase()) {
            "shift" -> this.alg = EncryptionAlgorithm.SHIFT
            "unicode" -> this.alg = EncryptionAlgorithm.UNICODE
            else -> {
                println("Error: Invalid algorithm")
                exitProcess(-1)
            }
        }
    }

    fun parseData() {
        if (this.data.isNotEmpty()) {
            this.input = this.data
        } else if (this.srcFileName != null) {
            val srcFile = this.srcFileName?.let { File(it) }
            if (srcFile != null) {
                this.input = try {
                    srcFile.readText()
                } catch (e: FileNotFoundException) {
                    println("Error: File does not exist")
                    exitProcess(-1)
                }
            }
        } else {
            println("Error: must provide a data source")
            exitProcess(-1)
        }
    }

    fun encryptStringWithKey(): String {
        return when (this.alg) {
            EncryptionAlgorithm.SHIFT -> encryptShift()
            EncryptionAlgorithm.UNICODE -> encryptUnicode()
        }
    }

    private fun encryptShift(): String {
        val builder = StringBuilder()
        for (c in this.input) {
            if(c.isLowerCase()) {
                val newPos = (c.code - 97 + key) % 26
                val newChar = Char(newPos + 97)
                builder.append(newChar)
            } else if (c.isUpperCase()) {
                val newPos = (c.code - 65 + key) % 26
                val newChar = Char(newPos + 65)
                builder.append(newChar)
            } else {
                builder.append(c)
            }
        }
        this.output = builder.toString()
        return this.output
    }

    private fun encryptUnicode(): String {
        val builder = StringBuilder()
        for(c in this.input) {
            val newChar: Char = c + key
            builder.append(newChar)
        }

        this.output = builder.toString()
        return this.output
    }

    fun decryptStringWithKey(): String {
        return when (this.alg) {
            EncryptionAlgorithm.SHIFT -> decryptShift()
            EncryptionAlgorithm.UNICODE -> decryptUnicode()
        }
    }

    private fun decryptShift(): String {
        val builder = StringBuilder()
        for (c in this.input) {
            if(c.isLowerCase()) {
                val newPos = c.code - key
                val newChar = if (newPos < 97) Char(newPos + 26) else Char(newPos)
                builder.append(newChar)
            } else if (c.isUpperCase()) {
                val newPos = c.code - 65 - key
                val newChar = if (newPos < 65) Char(newPos + 26) else Char(newPos)
                builder.append(newChar)
            } else {
                builder.append(c)
            }
        }
        this.output = builder.toString()
        return this.output
    }

    private fun decryptUnicode(): String {
        val builder = StringBuilder()
        for (c in this.input) {
            val newChar: Char = c - key
            builder.append(newChar)
        }

        this.output = builder.toString()
        return this.output
    }

    fun handleOutput() {
        if (this.desFileName != null) {
            desFileName?.let { File(it) }?.writeText(this.output)
        } else {
            println(output)
        }
    }

    enum class Mode {
        ENC,
        DEC
    }

    enum class EncryptionAlgorithm {
        UNICODE,
        SHIFT
    }
}