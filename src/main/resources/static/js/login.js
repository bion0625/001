function signUp(){
    loadingFormStart();
    location.href="/join";
}

function login(){
    loadingFormStart();
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
            loadingFormEnd();
            if(data.loginCheck === "SUCCESS"){
                location.href="/main";
            }else{
                alert(data.loginCheck);
            }
        });
}