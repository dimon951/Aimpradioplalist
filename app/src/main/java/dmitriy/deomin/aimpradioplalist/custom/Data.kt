package dmitriy.deomin.aimpradioplalist.custom

data class Radio(val name: String, val url: String,val kbps:String="",val kategory:String="")

data class Link(val kbps: String, val url: String)

data class RadioPop(val name: String,
                    val ava_url: String,
                    val link1: Link,
                    val link2: Link,
                    val link3: Link,
                    val link4: Link,
                    val link5: Link)

