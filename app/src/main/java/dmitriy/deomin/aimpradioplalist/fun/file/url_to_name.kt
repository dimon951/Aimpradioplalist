package dmitriy.deomin.aimpradioplalist.`fun`.file

fun url_to_name(url:String):String{

if(url.contains(".")){
    return clear_name_ot_chlama(url.substringAfterLast("."))
}else{
    return clear_name_ot_chlama(url)
}
}