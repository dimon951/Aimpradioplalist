package dmitriy.deomin.aimpradioplalist.custom

data class Radio(val name: String, val kategory:String="",
                 val link1: Link,
                 val link2: Link= Link("",""),
                 val link3: Link=Link("",""),
                 val link4: Link=Link("",""),
                 val link5: Link=Link("",""),
                 val link6: Link=Link("",""))

data class Link(val kbps: String, val url: String)

data class RadioPop(val name: String,
                    val ava_url: String,
                    val link1: Link,
                    val link2: Link,
                    val link3: Link,
                    val link4: Link,
                    val link5: Link)

