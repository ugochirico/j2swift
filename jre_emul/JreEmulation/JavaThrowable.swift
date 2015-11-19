//
//  JavaThrowable.swift
//  JreEmulation
//
//  Created by 徐涛 on 11/19/15.
//  Copyright © 2015 kingxt. All rights reserved.
//

import Foundation

public class JavaThrowable : JavaObject, ErrorType {
    
    private var detailMessage:String?
    
    private var cause:JavaThrowable? = nil
    
    public override init() {
    }
    
    public init(_ message:String?) {
        super.init()
        self.detailMessage = message;
    }
    
    public init(_ message:String?, _ cause:JavaThrowable) {
        super.init()
        self.detailMessage = message
        self.cause = cause
    }
    
    public func getMessage() -> String? {
        return detailMessage
    }
    
    public func getLocalizedMessage() -> String? {
        return getMessage()
    }
}