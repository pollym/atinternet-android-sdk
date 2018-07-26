/*
This SDK is licensed under the MIT license (MIT)
Copyright (c) 2015- Applied Technologies Internet SAS (registration number B 403 261 258 - Trade and Companies Register of Bordeaux – France)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.atinternet.tracker.ecommerce;

import com.atinternet.tracker.Event;
import com.atinternet.tracker.Screen;
import com.atinternet.tracker.Tracker;
import com.atinternet.tracker.TrackerConfigurationKeys;
import com.atinternet.tracker.Utility;
import com.atinternet.tracker.ecommerce.objectproperties.Cart;
import com.atinternet.tracker.ecommerce.objectproperties.Customer;
import com.atinternet.tracker.ecommerce.objectproperties.Payment;
import com.atinternet.tracker.ecommerce.objectproperties.Product;
import com.atinternet.tracker.ecommerce.objectproperties.Shipping;
import com.atinternet.tracker.ecommerce.objectproperties.Transaction;

import java.util.ArrayList;
import java.util.Map;

public class TransactionConfirmation extends EcommerceEvent {

    private Tracker tracker;

    private Cart cart;
    private java.util.List<String> promotionalCodes;
    private Transaction transaction;
    private Shipping shipping;
    private Payment payment;
    private Customer customer;
    private java.util.List<Product> products;


    TransactionConfirmation(Tracker tracker, Screen screen) {
        super("transaction.confirmation", screen);
        this.tracker = tracker;
        cart = new Cart();
        promotionalCodes = new ArrayList<>();
        transaction = new Transaction();
        shipping = new Shipping();
        payment = new Payment();
        customer = new Customer();
        products = new ArrayList<>();
    }

    public Cart Cart() {
        return cart;
    }

    public java.util.List<String> PromotionalCodes() {
        return promotionalCodes;
    }

    public Transaction Transaction() {
        return transaction;
    }

    public Shipping Shipping() {
        return shipping;
    }

    public Payment Payment() {
        return payment;
    }

    public Customer Customer() {
        return customer;
    }

    public java.util.List<Product> Products() {
        return products;
    }

    @Override
    protected Map<String, Object> getData() {
        data.put("cart", cart);
        data.put("payment", payment);
        data.put("customer", customer);
        data.put("shipping", shipping);
        data.put("transaction", transaction);
        data.put("a:s:promotionalCode", promotionalCodes);
        return super.getData();
    }

    @Override
    protected java.util.List<Event> getAdditionalEvents() {
        /// SALES INSIGHTS
        java.util.List<Event> generatedEvents = super.getAdditionalEvents();

        CartConfirmation cc = new CartConfirmation(screen);
        cc.Transaction().set(transaction);
        cc.Cart().set(cart);
        generatedEvents.add(cc);

        for (Product p : products) {
            ProductPurchased pp = new ProductPurchased(screen);
            pp.Cart().put("id", String.valueOf(cart.get("s:id")));
            pp.Transaction().put("id", String.valueOf(transaction.get("s:id")));
            pp.Product().set(p);
            generatedEvents.add(pp);
        }

        /// SALES TRACKER
        if (Utility.parseBooleanFromString(String.valueOf(tracker.getConfiguration().get(TrackerConfigurationKeys.AUTO_SALES_TRACKER)))) {
            double turnoverTaxIncluded = Utility.parseDoubleFromString(String.valueOf(cart.get("f:turnoverTaxIncluded"))),
                    turnoverTaxFree = Utility.parseDoubleFromString(String.valueOf(cart.get("f:turnoverTaxFree")));

            String[] codes = new String[promotionalCodes.size()];
            promotionalCodes.toArray(codes);
            tracker.Orders().add(String.valueOf(transaction.get("s:id")), Utility.parseDoubleFromString(String.valueOf(cart.get("f:turnoverTaxIncluded"))))
                    .setStatus(3).setPaymentMethod(0).setConfirmationRequired(false).setNewCustomer(Utility.parseBooleanFromString(String.valueOf(customer.get("b:new"))))
                    .Delivery().set(Utility.parseDoubleFromString(String.valueOf(shipping.get("f:costTaxFree"))), Utility.parseDoubleFromString(String.valueOf(shipping.get("f:costTaxIncluded"))), String.valueOf(shipping.get("s:delivery")))
                    .Amount().set(turnoverTaxFree, turnoverTaxIncluded, turnoverTaxIncluded - turnoverTaxFree)
                    .Discount().setPromotionalCode(Utility.stringJoin('|', codes));

            com.atinternet.tracker.Cart stCart = tracker.Cart().set(String.valueOf(cart.get("s:id")));
            for (Product p : products) {
                /// SALES TRACKER
                String stProductId;
                if (p.containsKey("s:name")) {
                    stProductId = String.format("%s[%s]", String.valueOf(p.get("s:id")), String.valueOf(p.get("s:name")));
                } else {
                    stProductId = String.valueOf(p.get("s:id"));
                }

                com.atinternet.tracker.Product stProduct = stCart.Products().add(stProductId)
                        .setQuantity(Utility.parseIntFromString(String.valueOf(p.get("n:quantity"))))
                        .setUnitPriceTaxIncluded(Utility.parseDoubleFromString(String.valueOf(p.get("f:priceTaxIncluded"))))
                        .setUnitPriceTaxFree(Utility.parseDoubleFromString(String.valueOf(p.get("f:priceTaxFree"))));

                Object stCategory = p.get("s:category1");
                if (stCategory != null) {
                    stProduct.setCategory1(String.format("[%s]", String.valueOf(stCategory)));
                }
                stCategory = p.get("s:category2");
                if (stCategory != null) {
                    stProduct.setCategory2(String.format("[%s]", String.valueOf(stCategory)));
                }
                stCategory = p.get("s:category3");
                if (stCategory != null) {
                    stProduct.setCategory3(String.format("[%s]", String.valueOf(stCategory)));
                }
                stCategory = p.get("s:category4");
                if (stCategory != null) {
                    stProduct.setCategory4(String.format("[%s]", String.valueOf(stCategory)));
                }
                stCategory = p.get("s:category5");
                if (stCategory != null) {
                    stProduct.setCategory5(String.format("[%s]", String.valueOf(stCategory)));
                }
                stCategory = p.get("s:category6");
                if (stCategory != null) {
                    stProduct.setCategory6(String.format("[%s]", String.valueOf(stCategory)));
                }

            }
            screen.setTimestamp(System.nanoTime());
            screen.setCart(stCart);
            screen.setIsBasketScreen(false).sendView();
            screen.setCart(null);
            stCart.unset();
        }

        return generatedEvents;
    }
}