var a = $("a");
for (let index = 0; index < a.length; index++) {
    var ad = a[index];
    if (ad.hasAttribute("target")){
        ad.removeAttribute("target");
    }
}
let myMap = new Map();
var a = $("video");
// 使用 find() 方法找到 source 元素
var sourceElements = a.find('source');

// 遍历每个 source 元素并输出其 src 属性
sourceElements.each(function(index, element) {
  var sourceSrc = $(element).attr('src');
  myMap.set(a[index],sourceSrc);
});
// 迭代 Map 的键值对
for (let [key, value] of myMap.entries()) {
  $.post('video://player/create', {data: getHashCode(key)+"->"+value });
}
setInterval (updateVideos, 1000);

function updateVideos(){

    for (let [key, value] of myMap.entries()) {
      // 获取元素的屏幕位置
          var rect = key.getBoundingClientRect();
          try{
            //$.post('video://player/move', {data: getHashCode(key)+"->"+rect.left+screenX+"->"+rect.top+screenY });
            $.post('video://player/move', {data: getHashCode(key)+"->"+rect.left+"->"+rect.top });
          }catch(e){}
    }
}
// 生成变量的哈希码
function getHashCode(variable) {
  let hash = 0;

  if (typeof variable === 'object') {
    // 如果是对象，将其序列化为字符串后再进行哈希计算
    variable = JSON.stringify(variable);
  } else {
    // 如果是其他类型，将其转换为字符串
    variable = variable.toString();
  }

  // 哈希计算
  for (let i = 0; i < variable.length; i++) {
    let char = variable.charCodeAt(i);
    hash = (hash << 5) - hash + char;
    hash |= 0; // 转为32位整数
  }

  return hash;
}