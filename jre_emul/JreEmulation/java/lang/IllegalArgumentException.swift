/*
 *This is generated by J2Swift, donot modify 
 */


import Foundation

public class JavaIllegalArgumentException : JavaRuntimeException {

  private static let serialVersionUID:jlong = -5365630128856068164


  public override init()  {
    
    super.init()
  }

  public override init(_ detailMessage:String?)  {
    
    super.init(detailMessage)
  }

  public override init(_ message:String?, _ cause:JavaThrowable?)  {
    
    super.init(message,cause)
  }

  public override init(_ cause:JavaThrowable?)  {
    
    super.init(cause == nil ? nil : cause!.toString(),cause)
  }


}