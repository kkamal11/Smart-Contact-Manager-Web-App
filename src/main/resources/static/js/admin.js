//Code to write

//Image preview when uploaded
const uploadedImage = document.querySelector("#image_file_input");

uploadedImage.addEventListener('change', (event) => {
    let imageFile = event.target.files[0];
    let reader = new FileReader();
    reader.onload = function () {
        const imgagePreview = document.querySelector("#upload_image_preview");
        imgagePreview.setAttribute("src", reader.result); //imgagePreview.src = reader.result;
    };
    reader.readAsDataURL(imageFile);
})
