//
//  JavaArrayExtension.swift
//  JreEmulation
//
//  Created by 徐 涛 on 15/11/21.
//  Copyright © 2015年 kingxt. All rights reserved.
//

import Foundation

extension Array {
    public static func getClass() -> JavaClass {
        let type: Mirror = Mirror(reflecting: self)
        return JavaClass(mirror: type)
    }
}