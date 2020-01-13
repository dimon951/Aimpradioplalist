package dmitriy.deomin.aimpradioplalist


import dmitriy.deomin.aimpradioplalist.custom.Radio

fun read_and_pars_m3u_file(url:String):ArrayList<Radio>{

    val data = File_function().readFile( url)

    return if(data.isNotEmpty()) m3u_parser(data) else{
        arrayListOf(Radio(name=Main.PUSTO,url=""))
    }
}