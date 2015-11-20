# J2Swift 已知问题记录



### 1.java中的类中属性的定义

java 中的如下代码：

``` java
public class A {
  public int i;
}

public class B extends A {
  public int i;
}
```

如果不做额外处理 翻译出来swift之后会是：

``` swift
class A {
  public var i : jint = 0
}

class B : A {
  public var i : jint = 0
}
```

但是在swift中上述情况是无法通过编译的，会有一个  `Cannot override with a stored property i`  错误。

目前的初步解决方法为（未实现）：

``` swift
class A {
  public var A_i : jint = 0
  public var i : jint {
  	get{
  		return A_i
	}
    set {
  		A_i = newValue
	}
  }
}

class B : A {
  public var B_i
  public override var i : jint {
  	get {
  		return B_i
	}
    set {
  		B_i = newValue
	}
  }
}
```

Authored by Dylan

2015.11.20