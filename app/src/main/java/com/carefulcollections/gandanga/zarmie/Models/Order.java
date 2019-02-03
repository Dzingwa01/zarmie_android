package com.carefulcollections.gandanga.zarmie.Models;

import java.util.List;

/**
 * Created by Gandanga on 2019-01-28.
 */

public class Order {

    public String name,surname, address,phone_number,item_name,item_category,bread_type,toast_type,delivery_or_collect,extra_instructions,delivery_time;
    public List<String> ingredients,toppings,drinks;
    public String order_date;
    public int quantity,id;
    public double prize;

    public Order(int id,String name,String surname,String address,String phone_number,String item_name,String item_category,int quantity,String bread_type,String toast_type,double prize,String extra_instructions,String delivery_time,List<String> ingredients,List<String> toppings,List<String> drinks,String order_date,String delivery_or_collect){
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phone_number = phone_number;
        this.item_name = item_name;
        this.item_category = item_category;
        this.bread_type = bread_type;
        this.prize = prize;
        this.toast_type = toast_type;
        this.extra_instructions = extra_instructions;
        this.delivery_time = delivery_time;
        this.ingredients = ingredients;
        this.toppings = toppings;
        this.drinks = drinks;
        this.quantity = quantity;
        this.order_date = order_date;
        this.id = id;
        this.delivery_or_collect = delivery_or_collect;
    }

}
