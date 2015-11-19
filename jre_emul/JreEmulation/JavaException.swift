//
//  JavaException.swift
//  JreEmulation
//
//  Created by 徐涛 on 11/19/15.
//  Copyright © 2015 kingxt. All rights reserved.
//

import Foundation

public class JavaException : JavaThrowable {
    
    public override init() {
        super.init()
    }
    
    public override init(_ message: String?) {
        super.init(message)
    }
    
    public override init(_ message: String?, _ cause: JavaThrowable) {
        super.init(message, cause)
    }
}