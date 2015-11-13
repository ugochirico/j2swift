//
//  JreEmulation.h
//  JreEmulation
//
//  Created by 徐涛 on 11/13/15.
//  Copyright © 2015 kingxt. All rights reserved.
//

#import <UIKit/UIKit.h>

//! Project version number for JreEmulation.
FOUNDATION_EXPORT double JreEmulationVersionNumber;

//! Project version string for JreEmulation.
FOUNDATION_EXPORT const unsigned char JreEmulationVersionString[];

// In this header, you should import all the public headers of your framework using statements like #import <JreEmulation/PublicHeader.h>

// Typedefs for each of Java's primitive types. (as originally defined in jni.h)
// jboolean and jbyte are modified from the original jni.h to integrate better
// with Objective-C code.
typedef int8_t          jbyte;          /* signed 8 bits */
typedef uint16_t        jchar;          /* unsigned 16 bits */
typedef int16_t         jshort;         /* signed 16 bits */
typedef int32_t         jint;           /* signed 32 bits */
typedef int64_t         jlong;          /* signed 64 bits */
typedef float           jfloat;         /* 32-bit IEEE 754 */
typedef double          jdouble;        /* 64-bit IEEE 754 */

#if defined(__OBJC__) || defined(__cplusplus__)
typedef bool            jboolean;
#else
typedef uint8_t         jboolean;
#endif
