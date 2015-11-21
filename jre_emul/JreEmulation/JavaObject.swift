//
//  Object.swift
//  JreEmulation
//
//  Created by 徐涛 on 11/13/15.
//  Copyright © 2015 kingxt. All rights reserved.
//

import Foundation

public class JavaObject : Equatable, AnyObject {
    
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
    
    public func toString() -> String? {
        return "\(self)@\(self.hashCode())"
    }
    
    public func hashCode() ->jint {
        return ObjectIdentifier(self).hashValue as jint
    }
    
    public func equals(o:JavaObject?) ->jboolean  {
        return self == o
    }
    
}

public func == (lhs: JavaObject, rhs: JavaObject) -> jboolean {
    if ObjectIdentifier(lhs) == ObjectIdentifier(rhs) {
        return true
    } else {
        return false
    }
}

public func == (lhs: JavaObject?, rhs: JavaObject?) -> jboolean {
    if lhs == nil && rhs == nil {
        return true
    }
    if lhs == nil && rhs != nil {
        return false
    }
    if lhs != nil && rhs == nil {
        return false
    }
    return lhs! == rhs!
}
