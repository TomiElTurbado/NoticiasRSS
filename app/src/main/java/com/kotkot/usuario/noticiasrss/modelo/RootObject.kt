package com.kotkot.usuario.noticiasrss.modelo

/**
 * Created by usuario on 15/02/18.
 */

data class RootObject (val status:String, val feed:Feeder, val items:List<Item>)
