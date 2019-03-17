package dmitriy.deomin.aimpradioplalist.custom

data class Radio(val name: String, val kategory:String="", val kbps:String = "",val url:String)

data class Link(val kbps: String, val url: String)

data class RadioPop(val name: String,
                    val ava_url: String,
                    val link1: Link,
                    val link2: Link,
                    val link3: Link,
                    val link4: Link,
                    val link5: Link)

data class Theme(val name:String,val fon:Int,val item:Int,val text:Int,val text_context:Int)

data class History(val name:String,val data_time:String)

