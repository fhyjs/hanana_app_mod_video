var a = $("a");
for (let index = 0; index < a.length; index++) {
    var ad = a[index];
    if (ad.hasAttribute("target")){
        ad.removeAttribute("target");
    }
}
if (location.href.includes("video")) {
    alert("Maybe video!");
}