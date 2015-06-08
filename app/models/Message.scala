package models

/**
 * Created by ka-son on 6/4/15.
 */

/**
 * Room user
 * @param user String
 * @param read Boolean
 */
case class RoomUser(user: String,
                     read: Boolean)

/**
 * Coordinates
 * @param Long Double
 * @param Lang Double
 */
case class Coordinates(Long: Double, Lang: Double)

/**
 * Message
 * @param owner String
 * @param users Seq[RoomUser]
 * @param coordinates Seq[Int]
 * @param text String
 * @param created_at Long
 */
case class Message(owner: String,
                   users: Seq[RoomUser],
                   coordinates: Coordinates,
                   text: String,
                   created_at: Long)
