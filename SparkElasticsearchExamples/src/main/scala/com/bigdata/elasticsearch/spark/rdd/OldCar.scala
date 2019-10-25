package com.bigdata.elasticsearch.spark.rdd

//case class OldCar(maker: String, model: String, mileage: Int, manufacture_year: Int, engine_displacement: Int, engine_power: Int, body_type: String, color_slug: String, stk_year: Int, transmission: String, door_count: Int, seat_count: Int, fuel_type: String, date_created: String, date_last_seen: String, price_eur: Double)

case class OldCar(maker: String, model: String, mileage: Int, manufacture_year: Int, engine_displacement: Int,
                  engine_power: Int, body_type: String, color_slug: String, stk_year: String, transmission: String,
                  door_count: String, seat_count: String, fuel_type: String, date_created: String,
                  date_last_seen: String, price_eur: Double)