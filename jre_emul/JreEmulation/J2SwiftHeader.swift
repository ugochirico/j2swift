//
//  J2SwiftHeader.swift
//  JreEmulation
//
//  Created by 徐涛 on 11/16/15.
//  Copyright © 2015 kingxt. All rights reserved.
//

import Foundation

public typealias jint = Int
public typealias jshort = CShort
public typealias jbyte = CSignedChar
public typealias jchar = CUnsignedShort
public typealias jlong = CLongLong
public typealias jfloat = Float
public typealias jdouble = Double;
public typealias jboolean = Bool


infix operator >>> {
    associativity none
    precedence 160
}

func >>> (left:jint, right: jint) -> jint {
    //TODO
    return 0
}