/*
 *This is generated by J2Swift, donot modify 
 */


import Foundation

public class JavaInteger : JavaNumber, JavaComparable {
  private let value:jint

  private static let serialVersionUID:jlong = 1360826667806852920
  public static let MAX_VALUE:jint = jint(0x7FFFFFFF)
  public static let MIN_VALUE:jint = -0x7fffffff - 1
  public static let SIZE:jint = 32
  private static let NTZ_TABLE:[jbyte]? = [32, 0, 1, 12, 2, 6, -1, 13, 3, -1, 7, -1, -1, -1, -1, 14, 10, 4, -1, -1, 8, -1, -1, 25, -1, -1, -1, -1, -1, 21, 27, 15, 31, 11, 5, -1, -1, -1, -1, -1, 9, -1, -1, 24, -1, -1, 20, 26, 30, -1, -1, -1, -1, 23, -1, 19, 29, -1, 22, 18, 28, 17, 16, -1]
  public static let TYPE:JavaClass? = ([jint].getClass().getComponentType())
  private static let SMALL_VALUES:[JavaInteger?]? = 


  public init(withjint value:jint)  {
    self.value = value
    
    super.init()
  }

  public convenience init(withString string:String?) throws  {
    self.init(withjint: try JavaInteger.parseInt(string))
  }

  public override func byteValue() ->jbyte  {
    return jbyte(value)
  }

  public func compareTo(object:JavaInteger?) ->jint  {
    return JavaInteger.compare(value,object!.value)
  }

  public static func compare(lhs:jint, _ rhs:jint) ->jint  {
    return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1)
  }

  static func invalidInt(s:String?) throws ->JavaNumberFormatException?  {
    throw JavaNumberFormatException(withString: "Invalid int: \"\(s)\"")
  }

  public static func decode(string:String?) throws ->JavaInteger?  {
    var length:jint = string!.length()
    var i:jint = 0
    if (length == 0) {
      throw try JavaInteger.invalidInt(string)!
    }
    var firstDigit:jchar = string!.charAt(i)
    var negative:jboolean = firstDigit == "-".asciiValue
    if (negative) {
      if (length == 1) {
        throw try JavaInteger.invalidInt(string)!
      }
      firstDigit = string!.charAt(++i)
    }
    var base:jint = 10
    if (firstDigit == "0".asciiValue) {
      if (++i == length) {
        return JavaInteger.valueOf(0)
      }
      if (jchar(firstDigit = string!.charAt(i)) == "x".asciiValue || firstDigit == "X".asciiValue) {
        if (++i == length) {
          throw try JavaInteger.invalidInt(string)!
        }
        base = 16
      }
      else {
        base = 8
      }
    }
    else if (firstDigit == "#".asciiValue) {
      if (++i == length) {
        throw try JavaInteger.invalidInt(string)!
      }
      base = 16
    }
    var result:jint = try JavaInteger.parse(string,i,base,negative)
    return JavaInteger.valueOf(result)
  }

  public override func doubleValue() ->jdouble  {
    return (jdouble(value))
  }

  public override func equals(o:JavaObject?) ->jboolean  {
    return jboolean(o is JavaInteger) && jboolean(((o as! JavaInteger)).value == value)
  }

  public override func floatValue() ->jfloat  {
    return (jfloat(value))
  }

  public static func getInteger(string:String?) ->JavaInteger?  {
    if (string == nil || string!.length() == 0) {
      return nil
    }
    var prop:String? = JavaSystem.getProperty(string)
    if (prop == nil) {
      return nil
    }
    do {
      return try JavaInteger.decode(prop)
    }
    catch (let ex as JavaNumberFormatException) {
      return nil
    }
    catch {}
  }

  public static func getInteger(string:String?, _ defaultValue:jint) ->JavaInteger?  {
    if (string == nil || string!.length() == 0) {
      return JavaInteger.valueOf(defaultValue)
    }
    var prop:String? = JavaSystem.getProperty(string)
    if (prop == nil) {
      return JavaInteger.valueOf(defaultValue)
    }
    do {
      return try JavaInteger.decode(prop)
    }
    catch (let ex as JavaNumberFormatException) {
      return JavaInteger.valueOf(defaultValue)
    }
    catch {}
  }

  public static func getInteger(string:String?, _ defaultValue:JavaInteger?) ->JavaInteger?  {
    if (string == nil || string!.length() == 0) {
      return defaultValue
    }
    var prop:String? = JavaSystem.getProperty(string)
    if (prop == nil) {
      return defaultValue
    }
    do {
      return try JavaInteger.decode(prop)
    }
    catch (let ex as JavaNumberFormatException) {
      return defaultValue
    }
    catch {}
  }

  public override func hashCode() ->jint  {
    return value
  }

  public override func intValue() ->jint  {
    return value
  }

  public override func longValue() ->jlong  {
    return (jlong(value))
  }

  public static func parseInt(string:String?) throws ->jint  {
    return try JavaInteger.parseInt(string,10)
  }

  public static func parseInt(string:String?, _ radix:jint) throws ->jint  {
    if (radix < JavaCharacter.MIN_RADIX || radix > JavaCharacter.MAX_RADIX) {
      throw JavaNumberFormatException(withString: "Invalid radix: \(radix)")
    }
    if (string == nil) {
      throw try JavaInteger.invalidInt(string)!
    }
    var length:jint = string!.length()
    var i:jint = 0
    if (length == 0) {
      throw try JavaInteger.invalidInt(string)!
    }
    var negative:jboolean = string!.charAt(i) == "-".asciiValue
    if (negative && ++i == length) {
      throw try JavaInteger.invalidInt(string)!
    }
    return try JavaInteger.parse(string,i,radix,negative)
  }

  static func parse(string:String?, _ offset:jint, _ radix:jint, _ negative:jboolean) throws ->jint  {
    var max:jint = JavaInteger.MIN_VALUE / radix
    var result:jint = 0
    var length:jint = string!.length()
    while (offset < length) {
      var digit:jint = JavaCharacter.digit(string!.charAt(offset++),radix)
      if (digit == -1) {
        throw try JavaInteger.invalidInt(string)!
      }
      if (max > result) {
        throw try JavaInteger.invalidInt(string)!
      }
      var next:jint = result * radix - digit
      if (next > result) {
        throw try JavaInteger.invalidInt(string)!
      }
      result = next
    }
    if (!negative) {
      result = -result
      if (result < 0) {
        throw try JavaInteger.invalidInt(string)!
      }
    }
    return result
  }

  public override func shortValue() ->jshort  {
    return jshort(value)
  }

  public static func toBinaryString(i:jint) ->String?  {
    return JavaIntegralToString.intToBinaryString(i)
  }

  public static func toHexString(i:jint) ->String?  {
    return JavaIntegralToString.intToHexString(i,false,0)
  }

  public static func toOctalString(i:jint) ->String?  {
    return JavaIntegralToString.intToOctalString(i)
  }

  public override func toString() ->String?  {
    return JavaInteger.toString(value)
  }

  public static func toString(i:jint) ->String?  {
    return JavaIntegralToString.intToString(i)
  }

  public static func toString(i:jint, _ radix:jint) ->String?  {
    return JavaIntegralToString.intToString(i,radix)
  }

  public static func valueOf(string:String?) throws ->JavaInteger?  {
    return JavaInteger.valueOf(try JavaInteger.parseInt(string))
  }

  public static func valueOf(string:String?, _ radix:jint) throws ->JavaInteger?  {
    return JavaInteger.valueOf(try JavaInteger.parseInt(string,radix))
  }

  public static func highestOneBit(var i:jint) ->jint  {
    i |= (i >> 1)
    i |= (i >> 2)
    i |= (i >> 4)
    i |= (i >> 8)
    i |= (i >> 16)
    return i - jint(i >>> 1)
  }

  public static func lowestOneBit(i:jint) ->jint  {
    return i & -i
  }

  public static func numberOfLeadingZeros(var i:jint) ->jint  {
    if (i <= 0) {
      return jint(~i >> 26) & 32
    }
    var n:jint = 1
    if (i >> 16 == 0) {
      n += 16
      i <<= 16
    }
    if (i >> 24 == 0) {
      n += 8
      i <<= 8
    }
    if (i >> 28 == 0) {
      n += 4
      i <<= 4
    }
    if (i >> 30 == 0) {
      n += 2
      i <<= 2
    }
    return n - jint(i >>> 31)
  }

  public static func numberOfTrailingZeros(i:jint) ->jint  {
    return (jint(JavaInteger.NTZ_TABLE![jint(jint(i & -i) * jint(0x0450FBAF)) >>> 26]))
  }

  public static func bitCount(var i:jint) ->jint  {
    i -= jint(i >> 1) & jint(0x55555555)
    i = jint(i & jint(0x33333333)) + jint(jint(i >> 2) & jint(0x33333333))
    i = jint(jint(i >> 4) + i) & jint(0x0F0F0F0F)
    i += i >> 8
    i += i >> 16
    return i & jint(0x0000003F)
  }

  public static func rotateLeft(i:jint, _ distance:jint) ->jint  {
    return jint(i << distance) | jint(i >>> -distance)
  }

  public static func rotateRight(i:jint, _ distance:jint) ->jint  {
    return jint(i >>> distance) | jint(i << -distance)
  }

  public static func reverseBytes(var i:jint) ->jint  {
    i = jint(jint(i >>> 8) & jint(0x00FF00FF)) | jint(jint(i & jint(0x00FF00FF)) << 8)
    return jint(i >>> 16) | jint(i << 16)
  }

  public static func reverse(var i:jint) ->jint  {
    i = jint(jint(i >>> 1) & jint(0x55555555)) | jint(jint(i & jint(0x55555555)) << 1)
    i = jint(jint(i >>> 2) & jint(0x33333333)) | jint(jint(i & jint(0x33333333)) << 2)
    i = jint(jint(i >>> 4) & jint(0x0F0F0F0F)) | jint(jint(i & jint(0x0F0F0F0F)) << 4)
    i = jint(jint(i >>> 8) & jint(0x00FF00FF)) | jint(jint(i & jint(0x00FF00FF)) << 8)
    return jint((i >>> 16)) | jint(jint(i) << 16)
  }

  public static func signum(i:jint) ->jint  {
    return jint(i >> 31) | jint(-i >>> 31)
  }

  public static func valueOf(i:jint) ->JavaInteger?  {
    return i >= 128 || i < -128 ? JavaInteger(withjint: i) : JavaInteger.SMALL_VALUES![i + 128]
  }


}
