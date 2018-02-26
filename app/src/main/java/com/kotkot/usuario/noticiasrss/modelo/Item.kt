package com.kotkot.usuario.noticiasrss.modelo

/**
 * Created by usuario on 15/02/18.
 */

data class Item (val title:String, val pubDate:String, val link:String, val guid:String, val author:String, val thumbnail:String, val description:String, val content:String, val enclosure:Enclosure, val categories:List<String>)
