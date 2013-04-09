/**
 *
 */
package ru.kfu.itis.issst.uima.shaltef.mappings

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
trait DepToArgMappingsPostProcessor {
  
  def postprocess(mpBuilder:DepToArgMappingsBuilder)

}