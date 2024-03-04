package encryptdecrypt

import kotlin.system.exitProcess


fun main(args: Array<String>) {

    val encryptor = Encryptor()

    if (args.size >= 6) {
        encryptor.parseArguments(args)
    } else {
        println("Error: Incorrect number of arguments")
        exitProcess(-1)
    }

    encryptor.parseData()

    when (encryptor.mode) {
        Encryptor.Mode.ENC -> encryptor.encryptStringWithKey()
        Encryptor.Mode.DEC -> encryptor.decryptStringWithKey()
    }

    encryptor.handleOutput()
}

fun encryptLettersWithKey(input: String, key: Int): String {
    val builder = StringBuilder()
    for(c in input) {
        if(c.isLowerCase()) {
            val addKey = (c.code + key)
            val newChar: Char = if (addKey > 122) Char(addKey - 26) else Char(addKey)
            builder.append(newChar)
        } else {
            builder.append(c)
        }
    }

    return builder.toString()
}

fun encryptString(input: String): String {
    var encrypted = String()
    for(c in input.lowercase()) {
        if(c.isLowerCase()) {
            val diff = c.code - 97
            encrypted += 'z' - diff
        } else {
            encrypted += c
        }
        //encrypted += encryptionMap[c] ?: c
    }
    return encrypted
}