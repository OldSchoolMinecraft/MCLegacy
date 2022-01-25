$(document).ready(function () {
    $("#btnBuy").click(function () {
        swal({
            title: "Server Name",
            text: "Your server needs a name! Please enter in the box what you would like your server to be called.",
            content: "input",
            button: {
                text: "Next",
                closeModal: false
            }
        }).then((name) => {
            if (!name) swal.close();
            swal({
                title: "Server Domain",
                text: `Would you like a free mclegacy.net subdomain (example.mclegacy.net)?`,
                buttons: {
                    yes: {
                        text: "Yes, please!",
                        value: "yes",
                        icon: "success"
                    },
                    no: {
                        text: "Nah",
                        value: "no"
                    }
                },
            }).then((value) => {
                swal("Setup Complete", "Invoice generated. Activation upon payment.", "success");
            })
        });
    });
});