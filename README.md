### Why

This project plan to translate java to swift. J2objc is very nice project that can translate java to object-c with only loss very litte performace. swift code feather is very similar with java, that is my intension.

### Current Support

* Support all java7 control syntax and will add java8 block support
  
* Support  Java exception translation based on ErrorType
  
``` swift
  do {
        try testThrowStatement()
  }
  catch (let e as JavaException) {
  }
  catch {}
  defer {         // Like java finally
  }
```
  
* Support Java generic type
  
* Readable Code
  
  JDK Exception.java will be translated as follow.
  
``` swift
  
/*
  
   *This is generated by J2Swift, donot modify 
  
*/
import Foundation
  
public class JavaException : JavaThrowable {
  
   private static let serialVersionUID:jlong = -3387516993124229948

public override init()  {
  super.init()
}

public override init(_ detailMessage:String?)  {
  super.init(detailMessage)
}

public override init(_ detailMessage:String?, _ throwable:JavaThrowable?)  {
  super.init(detailMessage,throwable)
}

public override init(_ throwable:JavaThrowable?)  {
  super.init(throwable)
}

override init(_ message:String?, _ cause:JavaThrowable?, _ enableSuppression:jboolean, _ writableStackTrace:jboolean)  {
  super.init(message,cause,enableSuppression,writableStackTrace)
}

```

But there is some limit with java. As java hava runtime exception and swift must declare throws when throw exception, so when write your java code, if any method throw java exception, you should declare method has exception even though runtime exception.

* Support Java generic type
  
  ​

### TODO

* Anonymous class support
  
* Translate JDK to swift
  ​
    ​