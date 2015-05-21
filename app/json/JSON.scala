package json

import play.api.libs.json.{JsValue, Json}

/**
 * Created by kasonchan on 5/20/15.
 */
trait JSON {

  /**
   * Prettify
   * Return a string of readable json value
   * @param jv JsValue
   * @return String
   */
  def prettify(jv: JsValue): String = {
    Json.prettyPrint(jv)
  }

}
