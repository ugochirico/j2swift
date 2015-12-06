/*
 *This is generated by J2Swift, donot modify 
 */


import Foundation

public protocol JavaCollection : JavaIterable {
  typealias T
  func add(object:T?) ->jboolean
  func addAll<T:JavaCollection>(collection:T?) ->jboolean
  func clear()
  func contains(object:JavaObject?) ->jboolean
  func containsAll<T:JavaCollection>(collection:T?) ->jboolean
  func equals(object:JavaObject?) ->jboolean
  func hashCode() ->jint
  func isEmpty() ->jboolean
  func iterator() ->JavaIterator?
  func remove(object:JavaObject?) ->jboolean
  func removeAll<T:JavaCollection>(collection:T?) ->jboolean
  func retainAll<T:JavaCollection>(collection:T?) ->jboolean
  func size() ->jint
  func toArray() ->[JavaObject?]?
  func toArray(array:[JavaObject?]?) ->[JavaObject?]?

}
