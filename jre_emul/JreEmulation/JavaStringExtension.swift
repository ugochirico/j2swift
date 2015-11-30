//
//  JavaStringExtension.swift
//  JreEmulation
//
//  Created by Kingxt on 11/13/15.
//  Copyright © 2015 kingxt. All rights reserved.
//

import Foundation

extension Character {
    var unicodeScalarsValue: UInt16 {
        return UInt16(String(self).unicodeScalars.first!.value)
    }
}

public extension String {
    
    public var asciiValue: UInt16 {
        guard let first = characters.first where characters.count == 1 else  { return 0 }
        return first.unicodeScalarsValue
    }
    
    ////////////////////////////////////////////////////
    
    public func isEmpty()->Bool {
        return characters.count == 0;
    }
    
    /**
     * Compares this string to the specified object.  The result is {@code
     * true} if and only if the argument is not {@code null} and is a {@code
     * String} object that represents the same sequence of characters as this
     * object.
     
     * parameter anObject: anObject
     *         The object to compare this {@code String} against
     
     * returns: {@code true} if the given object represents a {@code String}
     *          equivalent to this string, {@code false} otherwise
     */
    public func equals(anObject:AnyObject!)->Bool {
        if anObject is String {
            let anotherString:String = anObject as! String
            if self == anotherString {
                return true
            }
        }
        return false
    }
    
    /**
     * the length of the sequence of characters represented by this
     *          object.
     */
    public func length() -> jint {
        return jint(characters.count)
    }
    
    public static func valueOf(obj:Any?) ->String? {
        if obj == nil {
            return nil;
        }
        let str:String = obj as! String
        return str
    }
    
    public func equalsIgnoreCase(another:String?) -> jboolean {
        if (another == nil) {
            return false
        }
        if(self.caseInsensitiveCompare(another!) == NSComparisonResult.OrderedSame){
            return true
        }
        //TODO
        return false
    }
    
    public func charAt(i:jint) -> jchar {
        return 0
    }
}