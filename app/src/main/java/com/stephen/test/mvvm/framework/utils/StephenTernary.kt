package com.stephen.test.mvvm.framework.utils

//Ternary三元运算
/**
 * if [T] is null run [nullTerm] else [term]
 */
fun <T, R> T?.forObj(term:(t:T)->R, nullTerm:()->R): R {
    return this.let{
        if(null == it) nullTerm()  else term(it!!)
    }
}

/**
 * if [T] is null return [nullObj] else [obj]
 */
fun <T, R> T?.forObj(obj:R, nullObj:R): R {
    return this.let{
        if(null == it) nullObj  else obj
    }
}

/**
 * if term is true run [trueTerm] else [falseTerm]
 */
fun <R> (()-> Boolean).doJudge(trueTerm:()->R, falseTerm:()->R): R {
    return if(this())  trueTerm()   else falseTerm()
}

/**
 * if true run [trueTerm] else [falseTerm]
 */
fun <R> Boolean.doJudge(trueTerm:()->R, falseTerm:()->R): R {
    return if(this)  trueTerm()   else falseTerm()
}

/**
 * if true return [trueObj] else [falseObj]
 */
fun <R> Boolean.doJudge(trueObj:R, falseObj:R): R {
    return if(this)  trueObj   else falseObj
}