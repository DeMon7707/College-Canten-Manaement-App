package com.example.lenovo.cafe_canteen.Model;

import java.util.List;

public class Request {
    private String phone;
    private String  name;
    private String time;
    private String total;
    private String paymentMethod;
    private String paymentstat;
    private List<Order> foods;  //list of food order
    private String status;

    public Request()
    {
    }

    public Request(String phone,String name,String time,String total,String paymentMethod,String paymentstat,List<Order> foods)
    {
        this.phone=phone;
        this.name=name;
        this.time=time;
        this.total=total;
        this.paymentMethod=paymentMethod;
        this.paymentstat=paymentstat;
        this.foods=foods;
        this.status="0";//Default is 0, 0: Placed, 1:Shipping ,2: Shipped
    }

    public String getPaymentstat() {
        return paymentstat;
    }

    public void setPaymentstat(String paymentstat) {
        this.paymentstat = paymentstat;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public String getTime() {
        return time;
    }

    public String getTotal() {
        return total;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setTime(String time) {
        this.time = time;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
