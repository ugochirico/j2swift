/*
 *This is generated by J2Swift, donot modify 
 */


import Foundation

public class JavaAssertionError : JavaError {

  private static let serialVersionUID:jlong = -5013299493970297370


  public override init()  {
    
    super.init()
  }

  public override init(withString detailMessage:String?, withJavaThrowable cause:JavaThrowable?)  {
    
    super.init(withString: detailMessage,withJavaThrowable: cause)
  }

  public init(withJavaObject detailMessage:JavaObject?)  {
    
    super.init(withString: String.valueOf(detailMessage))
    if (detailMessage is JavaThrowable) {
      initCause((detailMessage as! JavaThrowable))
    }
  }

  public init(withjboolean detailMessage:jboolean)  {
    
    super.init(withString: String.valueOf(detailMessage))
  }

  public init(withjchar detailMessage:jchar)  {
    
    super.init(withString: String.valueOf(detailMessage))
  }

  public init(withjint detailMessage:jint)  {
    
    super.init(withString: JavaInteger.toString(detailMessage))
  }

  public init(withjlong detailMessage:jlong)  {
    
    super.init(withString: JavaLong.toString(detailMessage))
  }

  public init(withjfloat detailMessage:jfloat)  {
    
    super.init(withString: JavaFloat.toString(detailMessage))
  }

  public init(withjdouble detailMessage:jdouble)  {
    
    super.init(withString: JavaDouble.toString(detailMessage))
  }


}
