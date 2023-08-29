// To hide and show sidebar


//ye function side bar cross icon and 'bar' icon pe call hoga
const toggleSidebar = () =>{
    if ($(".sidebar").is(":visible")) {
        // agar visible h to isko nhi dikhana h
        // 1) Display property ko 'none' karna hog ataki display gayab ho jaye
        $(".sidebar").css("display" ,"none");

        //2) content ka margin '0%' kar denge taki jb sidebar visible nhi ho to 'content' left se dikhe
        $(".content").css("margin-left","0%")
    }
    else {
        // just upper ka ulta karna h. 1) sidebar show karna h
        // i.e display ko block karna h and content ko right karna h
        $(".sidebar").css("display" ,"block");
        $(".content").css("margin-left","20%")
    }
};


// search contact function

const search = () => {
    // console.log("searching....");

    let query = $("#search-input").val();

    if (query == '') {
        // agar kuch user input nhi dalega tb 'search-result' wala class  ko  hide kar denge.
        $(".search-result").hide();
    } 
    else {
        console.log(query);

        // sending request to server
        let url = `http://localhost:8080/search/${query}`;

        fetch(url)
        .then((response) => {
            return response.json(); // url ko fetch karne ke bad 'url' is function me aa jayega and then wahi response ko json me convert karke return kar diya.
        })
        // above me 'json' form me jo return hua hoga wo 'data' me mil jayega.
        .then((data) => {
            // data .....
            console.log(data);
            // ye jo data aa rha h isko parse karke 'result' me dikha dena h.
            // result html ke form me hi jayega

            let text = `<div class ='list-group'>`;
            // data jo hoga yahan wo ek array me hoga(all contact info), usme se hmko bs name chahiye.
            data.forEach((contact) => {
                text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-action'>${contact.name}</a>`
                // href: agar kisi user pe click karenge tb kya hmko show karega.
            });

            text += `</div>`;

            // data ab html me convert ho gya i.e 'text' (dynamic data aa gya h javascript se.)
            // ab is 'text' ko apne search wale result me dena h.
            $(".search-result").html(text); // html me dal  ke search box ko show kar denge
            // agar koi query hoga(koi result)  tb'search-result' wala class  ko show karenge.
            $(".search-result").show();
        });

        
    }
}