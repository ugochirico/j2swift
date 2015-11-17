//
//  Utility.swift
//  JreEmulation
//
//  Created by 徐 涛 on 15/11/17.
//  Copyright © 2015年 kingxt. All rights reserved.
//

import Foundation

class Utility{
    class func classNameAsString(obj: Any) -> String {
        return _stdlib_getDemangledTypeName(obj).componentsSeparatedByString(".").last!
    }
}