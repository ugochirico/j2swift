/*
 *This is generated by J2Swift, donot modify 
 */


import Foundation

public class JavaStringIndexOutOfBoundsException : JavaIndexOutOfBoundsException {
  private static let serialVersionUID:jlong = -6762910422159637258


  public override init()  {
    
    super.init()
  }

  public init(withjint index:jint)  {
    
    super.init(withString: "String index out of range: \(index)")
  }

  public override init(withString detailMessage:String?)  {
    
    super.init(withString: detailMessage)
  }

  public convenience init(withString s:String?, withjint index:jint)  {
    self.init(withjint: s!.length(),withjint: index)
  }

  public init(withjint sourceLength:jint, withjint index:jint)  {
    
    super.init(withString: "length=\(sourceLength); index=\(index)")
  }

  public convenience init(withString s:String?, withjint offset:jint, withjint count:jint)  {
    self.init(withjint: s!.length(),withjint: offset,withjint: count)
  }

  public init(withjint sourceLength:jint, withjint offset:jint, withjint count:jint)  {
    
    super.init(withString: "length=\(sourceLength); regionStart=\(offset); regionLength=\(count)")
  }


}
