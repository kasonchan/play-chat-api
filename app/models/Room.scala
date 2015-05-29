package models

/**
 * Created by ka-son on 5/27/15.
 */

/**
 * Room
 * @param login String
 * @param avatar_url String
 * @param users Seq[String]
 * @param privacy String // default is private, public or private
 * @param created_at Long
 * @param updated_at Long
 */
case class Room(login: String,
                avatar_url: String,
                users: Seq[String],
                privacy: String,
                created_at: Long,
                updated_at: Long)
