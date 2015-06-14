/**
 * Created by z00036 on 2015/05/25.
 */
package object consts extends UserConsts with ErrorConsts {

  implicit def config2string(config: AppConfig) = config.getOrEmpty
  implicit def config2int(config: AppConfig) = config.asInt
  implicit def config2boolean(config: AppConfig) = config.asBoolean

}
