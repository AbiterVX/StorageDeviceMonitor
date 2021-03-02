//界面跳转
function FJump(index) {
    if(index===1){
        window.location.href="/";
    }
    else if(index===2){
        window.location.href="CheckHostInfo";
    }
    else if(index===3){
        window.location.href="Test";
    }
}

//设置主机IP列表
function FSetHostIpList(hostIpList_htmlId){
    var jsonData = FGetHostIpList();
    if(jsonData != null){
        var hostIpList = document.getElementById(hostIpList_htmlId);
        var HostIpListHtml = ""
        for(var i=0;i< jsonData.length;i++){
            HostIpListHtml +=
                '<li class="nav-item">' +
                '   <a class="nav-link" href="#" ' +
                '       <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-layers"><polygon points="12 2 2 7 12 12 22 7 12 2"></polygon><polyline points="2 17 12 22 22 17"></polyline><polyline points="2 12 12 17 22 12"></polyline></svg>' +
                jsonData[i] +
                '   </a>' +
                '</li>'
        }
        //onclick="FGetHostInfo('+ i +')">
        hostIpList.innerHTML = HostIpListHtml;
    }
}
//初始化设置下拉菜单
function FSetDropDownMenu(HostIpDropDownBtnId, HostIpDropDownMenuId,selectBtnCallback){
    //设置下拉选项
    var hostIpDropDownMenu = document.getElementById(HostIpDropDownMenuId);
    var hostIpDropDownMenuHtml = ""
    var jsonData = FGetHostIpList();
    for(var i=0;i< jsonData.length;i++){
        hostIpDropDownMenuHtml +=
            "<a class=\"dropdown-item\" href=\"#\" onclick=\"FSelectHost('"+HostIpDropDownBtnId  +"'," + i + ","+ selectBtnCallback +")\">" + jsonData[i] +"</a>"
    }
    hostIpDropDownMenu.innerHTML = hostIpDropDownMenuHtml;
    //下拉按钮当前显示Txt
    var hostIpDropDownBtn = document.getElementById(HostIpDropDownBtnId);
    hostIpDropDownBtn.innerText = jsonData[0];
}
//下拉按钮选项，onclik ：选择Host
function FSelectHost(HostIpDropDownBtnId,currentHostIpIndex,func){
    var jsonData = FGetHostIpList();
    var hostIpDropDownBtn = document.getElementById(HostIpDropDownBtnId);
    hostIpDropDownBtn.innerText = jsonData[currentHostIpIndex];
    func(currentHostIpIndex);
}
//主机IP
function FGetHostIpList(){
    var jsonData = null;

    var sessionJsonData = window.sessionStorage.getItem("HostIpList");
    if(sessionJsonData != null){
        jsonData = JSON.parse(sessionJsonData);
    }
    else{
        $.ajax({
            type:"post",
            dataType:"json",
            url:"/getHostIpList",
            processData :false,
            contentType:"application/json",
            async:false,
            success:function (resultJsonData) {
                jsonData = resultJsonData;
                window.sessionStorage.setItem("HostIpList",JSON.stringify(jsonData))
            },
            error: function (err) {
            }
        });
    }


    return jsonData;
}
//[定时更新]获取主机信息
function FGetHostInfoList() {
    var jsonData = null;
    $.ajax({
        type:"post",
        dataType:"json",
        url:"/getHostInfoList",
        processData :false,
        contentType:"application/json",
        async:false,
        success:function (resultJsonData) {
            jsonData = resultJsonData;
        },
        error: function (err) {
        }
    });
    return jsonData;
}

//获取host硬件信息
function FGetHostHardWareInfoList(){
    var jsonData = null;
    var sessionJsonData = window.sessionStorage.getItem("HostHardWareInfo");
    if(sessionJsonData != null){
        jsonData = JSON.parse(sessionJsonData);
    }
    else{
        $.ajax({
            type:"post",
            dataType:"json",
            url:"/getHostHardWareList",
            processData :false,
            contentType:"application/json",
            async:false,
            success:function (resultJsonData) {
                jsonData = resultJsonData;
                window.sessionStorage.setItem("HostHardWareInfo",JSON.stringify(jsonData))
            },
            error: function (err) {
            }
        });
    }
    return jsonData;
}