package dmitriy.deomin.aimpradioplalist.`fun`.file

fun long_name_resize(s:String):String{
    if(s.length>90){
        return  s.substring(0,90)
    }else{
        return s
    }
}