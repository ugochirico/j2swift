//
//  Object.swift
//  JreEmulation
//
//  Created by 徐涛 on 11/13/15.
//  Copyright © 2015 kingxt. All rights reserved.
//

import Foundation

public class JavaObject {
    
    public init() {
    }
    
    public static func getClass() -> JavaClass {
        let type: Mirror = Mirror(reflecting: self)
        return JavaClass(mirror: type)
    }
    
    public func getClass() -> JavaClass {
        let type: Mirror = Mirror(reflecting: self)
        return JavaClass(mirror: type)
    }
}