var Lab4 = ( function() {

    return {

        convert: function(rates) {
            
            /*
             * This function accepts the data object sent by the server
             * ("rates") as an argument.  It should: get the amount (in USD)
             * entered by the user in the "input" form field, iterate through
             * the currency exchange rates given in the data object, multiply
             * each rateby the given number of U.S. Dollars, and compute the
             * corresponding amount for each currency.  These amounts should be
             * shown in the "output" element of the page, along with the
             * currency codes, separated by colons and formatted to two decimal
             * places.  (See the screenshot given with this assignment.)
             */
            
            //Get USD Amount to convert
            var usdAmount = parseInt($("#input").val());

            //Check if a valid number was entered in the usdAmount box
            if(!isNaN(usdAmount)){
                
                //Remove existing text (jQuery Version, other conversions,etc.)
                $("#output").empty();

                //Get rates from input
                var rateObject = rates["rates"];

                //Loop through rates
                for (var key in rateObject){
                    var base = key;
                    var rate = rateObject[key];

                    //Get final exchange rate and round it
                    var finalRate = (parseFloat(rate) * usdAmount).toFixed(2);
                    //var roundedRate = finalRate.toFixed(2);

                    $("#output").append(base + ": " + finalRate.toString() + "<br>");
                }
                
                $("#output").append("<br> Based on " + rates["date"] + " Exchange Rates");
            }
        },
        
        getConversion: function() {
            
            /*
             * This method should send an Ajax request to our API to get the
             * latest exchange rates.  Use "latest" as the URL and "json" as the
             * data type, so that the data will be automatically parsed to a
             * JavaScript object.  (In the sample code in the "HTTP Basics"
             * lecture notes, this object is called "response".  Then, invoke
             * the helper function "convert()" in the callback function to 
             * perform the conversion.  (If you are unclear about the purpose of
             * the "that" variable shown here, see Page 6 of the "Functions and
             * Objects" lecture notes.
             */
            
            var that = this;
            
            $.ajax({
                url: 'latest',
                method: 'GET',
                dataType: 'json',
                success: function(response) {
                    that.convert(response);             
                }
            });
            
        },
        
        init: function() {
            
            /* Output the current version of jQuery (for diagnostic purposes) */
            
            $('#output').html( "jQuery Version: " + $().jquery );
 
        }

    };

}());