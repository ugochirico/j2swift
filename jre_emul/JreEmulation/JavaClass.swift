//
//  Class.swift
//  JreEmulation
//
//  Created by 徐涛 on 11/13/15.
//  Copyright © 2015 kingxt. All rights reserved.
//

import Foundation

public class JavaClass {
    
    let mirror:Mirror
    
    init(mirror:Mirror) {
        self.mirror = mirror;
    }
    
    public func getName() -> String {
        return "\(mirror.subjectType)"
    }
    
    public func newInstance() -> AnyObject? {
        return nil
    }
    
    public func getComponentType() -> JavaClass? {
        return nil;
    }
}
