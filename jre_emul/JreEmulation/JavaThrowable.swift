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
    
    public init(withString message:String?) {
        super.init()
        self.detailMessage = message;
    }
    
    public init(withString message:String?, withJavaThrowable cause:JavaThrowable?) {
        super.init()
        self.detailMessage = message
        self.cause = cause
    }
    
    public init(withJavaThrowable cause:JavaThrowable?) {
        detailMessage = cause?.toString()
        self.cause = cause;
    }

    init(withString message:String?, withJavaThrowable cause:JavaThrowable?, withjboolean enableSuppression:jboolean, withjboolean writableStackTrace:jboolean) {
        detailMessage = message;
        self.cause = cause;
    }
    
    public func getMessage() -> String? {
        return detailMessage
    }
    
    public func getLocalizedMessage() -> String? {
        return getMessage()
    }
    
    public func initCause(cause:JavaThrowable?) -> JavaThrowable? {
        return nil;
    }
    
    public override func toString() -> String? {
        let s:String = getClass().getName()
        let message = getLocalizedMessage()
        if message != nil {
            return "\(s): \(message)";
        } else {
            return s;
        }
    }
}