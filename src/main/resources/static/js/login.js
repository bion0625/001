function join(){
    location.href="/join";
}

function login(){
    fetch('/', {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            userId: document.querySelector('#userId').value,
            userPassword: document.querySelector('#userPassword').value,
        }),
    }).then(data => data.json())
        .then(data => {
            if(data.loginCheck === "SUCCESS"){
                location.href="/main";
            }else{
                alert(data.loginCheck);
            }
        });
}