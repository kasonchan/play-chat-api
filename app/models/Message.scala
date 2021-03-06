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
 * @param latitude Double
 * @param longitude Double
 */
case class Coordinates(latitude: Double, longitude: Double)

/**
 * Message
 * @param owner String
 * @param users Seq[RoomUser]
 * @param coordinates Seq[Int]
 * @param text String
 * @param created_at Long
 */
case class Message(owner: String,
                   users: Seq[String],
                   reads: Seq[RoomUser],
                   coordinates: Coordinates,
                   text: String,
                   created_at: Long,
                   updated_at: Long)
