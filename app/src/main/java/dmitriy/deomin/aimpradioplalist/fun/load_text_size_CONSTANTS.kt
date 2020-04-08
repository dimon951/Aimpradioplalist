package dmitriy.deomin.aimpradioplalist.`fun`

import dmitriy.deomin.aimpradioplalist.Main

fun load_text_size_CONSTANTS(){

    Main.SIZE_TEXT_MAIN_BUTTON = if (save_read_float("SIZE_TEXT_MAIN_BUTTON") == 0F) {
        save_value_float("SIZE_TEXT_MAIN_BUTTON",14F)
        14F
    } else {
        save_read_float("SIZE_TEXT_MAIN_BUTTON")
    }

    Main.SIZE_TEXT_VSE_BUTTON = if (save_read_float("SIZE_TEXT_VSE_BUTTON") == 0F) {
        save_value_float("SIZE_TEXT_VSE_BUTTON",12F)
        12F
    } else {
        save_read_float("SIZE_TEXT_VSE_BUTTON")
    }

    Main.SIZE_TEXT_ONLINE_BUTTON = if (save_read_float("SIZE_TEXT_ONLINE_BUTTON") == 0F) {
        save_value_float("SIZE_TEXT_ONLINE_BUTTON",14F)
       14F
    } else {
        save_read_float("SIZE_TEXT_ONLINE_BUTTON")
    }

    Main.SIZE_TEXT_NAME = if (save_read_float("SIZE_TEXT_NAME") == 0F) {
        save_value_float("SIZE_TEXT_NAME",18F)
        18F
    } else {
        save_read_float("SIZE_TEXT_NAME")
    }

    Main.SIZE_TEXT_CONTEXT = if (save_read_float("SIZE_TEXT_CONTEXT") == 0F) {
        save_value_float("SIZE_TEXT_CONTEXT",14F)
        14F
    } else {
        save_read_float("SIZE_TEXT_CONTEXT")
    }

    Main.SIZE_TEXT_CONTEXT_text = if (save_read_float("SIZE_TEXT_CONTEXT_text") == 0F) {
        save_value_float("SIZE_TEXT_CONTEXT_text",10F)
       10F
    } else {
        save_read_float("SIZE_TEXT_CONTEXT_text")
    }

}