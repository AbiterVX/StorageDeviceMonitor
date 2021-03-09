//界面跳转
function FJump(index) {
    if(index===1){
        window.location.href="/";
    }
    else if(index===2){
        window.location.href="CheckHostInfo";
    }
    else if(index===3){
        window.location.href="CheckHostInfoComparison";
    }
    else if(index===4){
        window.location.href="CheckDiskFailureInfo";
    }
}


//初始化左侧导航栏HostIP列表
function FInitHostIpList(){
    FGetHostState(FHostStateCallback);
}




//服务器状态回调
function FHostStateCallback(hostIpList,hostStateList){
    var hostIpIndex = FGetCheckHostInfoJumpIndex();

    var selectedStyle = '';
    var jsonData = hostIpList;
    var hostState = hostStateList;
    if(jsonData != null){
        var hostIpList = document.getElementById("HostIpList");
        var hostDownColor = "";
        var HostIpListHtml = ""
        for(var i=0;i< jsonData.length;i++){
            if(hostState[i] === 0){
                hostDownColor = 'color="red"'
            }
            else{
                hostDownColor ='';
            }


            if(hostIpIndex == i && window.location.href.search("CheckHostInfo") != -1 && window.location.href.search("CheckHostInfoC") == -1){
                selectedStyle = 'style="color:black;font-weight:bold;" ';
            }
            else{
                selectedStyle = "";
            }

            HostIpListHtml +=
                '<li class="nav-item">' +
                '   <a class="nav-link" href="#" ' + selectedStyle +' onclick="FCheckHostInfoJump('+ i +')">' +
                '       <svg xmlns="http://www.w3.org/2000/svg" ' + hostDownColor +' width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-layers">' +
                '           <polygon points="12 2 2 7 12 12 22 7 12 2"></polygon>' +
                '           <polyline points="2 17 12 22 22 17"></polyline>' +
                '           <polyline points="2 12 12 17 22 12"></polyline>' +
                '       </svg>' +
                jsonData[i] +
                '   </a>' +
                '</li>'
        }
        hostIpList.innerHTML = HostIpListHtml;
    }
}

//获取服务器状态
function FGetHostState(callbackFunc){
    $.ajax({
        type:"post",
        dataType:"json",
        url:"/getHostState",
        processData :false,
        contentType:"application/json",
        async:true,
        success:function (resultJsonData) {
            var hostIpList = FGetHostIpList();
            callbackFunc(hostIpList,resultJsonData);
        },
        error: function (err) {
        }
    });
}


//初始化HostIp下拉菜单
function FInitHostIpDropDownMenu(selectBtnCallback,currentHostIpIndex){
    //设置下拉选项
    var hostIpDropDownMenu = document.getElementById("HostIpDropDownMenu");
    var hostIpDropDownMenuHtml = ""
    var jsonData = FGetHostIpList();
    for(var i=0;i< jsonData.length;i++){
        hostIpDropDownMenuHtml +=
            "<a class=\"dropdown-item\" href=\"#\" onclick=\"FSelectHost(" + i + ","+ selectBtnCallback +")\">" + jsonData[i] +"</a>"
    }
    hostIpDropDownMenu.innerHTML = hostIpDropDownMenuHtml;
    //下拉按钮当前显示Txt
    var hostIpDropDownBtn = document.getElementById("HostIpDropDownBtn");
    hostIpDropDownBtn.innerText = jsonData[currentHostIpIndex];
}
//HostIp下拉菜单按钮-onclik选择Host
function FSelectHost(hostIpIndex,callbackFunc){
    var jsonData = FGetHostIpList();
    var hostIpDropDownBtn = document.getElementById("HostIpDropDownBtn");
    hostIpDropDownBtn.innerText = jsonData[hostIpIndex];
    callbackFunc(hostIpIndex);
}


function FInitDateIntervalDropDownMenu(dateIntervalLabel,selectBtnCallback){
    var dateIntervalDropDownMenu = document.getElementById("DateIntervalDropDownMenu");
    var dateIntervalDropDownMenuHtml = ""
    var jsonData = FGetHostIpList();
    for(var i=0;i< dateIntervalLabel.length;i++){
        dateIntervalDropDownMenuHtml +=
            "<a class=\"dropdown-item\" href=\"#\" onclick=\"FSelectDateInterval(" + i +","+ selectBtnCallback +")\">" + dateIntervalLabel[i] +"</a>"
    }
    dateIntervalDropDownMenu.innerHTML = dateIntervalDropDownMenuHtml;
    //下拉按钮当前显示Txt
    var dateIntervalDropDownBtn = document.getElementById("DateIntervalDropDownBtn");
    dateIntervalDropDownBtn.innerText = dateIntervalLabel[0];
}
function FSelectDateInterval(index,selectBtnCallback){
    selectBtnCallback(index);
}

function FCheckHostInfoJump(hostIpIndex){
    FSetCheckHostInfoJumpIndex(hostIpIndex);
    window.location.href="CheckHostInfo";
}

//获取 CheckHostInfo界面 跳转的index
function FGetCheckHostInfoJumpIndex(){
    var checkHostInfoJumpIndex = window.sessionStorage.getItem("CheckHostInfoJumpIndex");
    if(checkHostInfoJumpIndex != null){
        return checkHostInfoJumpIndex;
    }
    else{
        window.sessionStorage.setItem("CheckHostInfoJumpIndex",0);
        return 0;
    }
}

//设置 CheckHostInfo界面 跳转的index
function FSetCheckHostInfoJumpIndex(index){
    window.sessionStorage.setItem("CheckHostInfoJumpIndex",index);
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
function FGetHostRealTimeInfo(callBackFunc) {
    $.ajax({
        type:"post",
        dataType:"json",
        url:"/getHostInfoList",
        processData :false,
        contentType:"application/json",
        async:true,
        success:function (resultJsonData) {
            callBackFunc(resultJsonData);
        },
        error: function (err) {
        }
    });
}

//获取host硬件信息
function FGetHostHardWareInfo(callBackFunc){
    var sessionJsonData = window.sessionStorage.getItem("HostHardWareInfo");
    if(sessionJsonData != null){
        var jsonData = JSON.parse(sessionJsonData);
        callBackFunc(jsonData);
    }
    else{
        $.ajax({
            type:"post",
            dataType:"json",
            url:"/getHostHardWareList",
            processData :false,
            contentType:"application/json",
            async:true,
            success:function (resultJsonData) {
                window.sessionStorage.setItem("HostHardWareInfo",JSON.stringify(resultJsonData));
                callBackFunc(resultJsonData);
            },
            error: function (err) {
            }
        });
    }

}

//时间戳转换
function FGetDateTime(currentDate){
    //时间转换
    var date = new Date(currentDate);
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    var d = date.getDate();
    var h = date.getHours();
    var min = date.getMinutes();
    var s = date.getSeconds();
    var dateString = [y, m, d].join('/') + " " + [h, min, s].join(':');
    //dateString = date.format("yyyy/MM/dd hh:mm:ss");
    return dateString;
}

//为数据添加单位Label
function FAddDataUnitLabel(data,unitlabel){
    var resultTxt;
    if(data !== "--"){
        resultTxt = data + unitlabel;
    }
    else{
        resultTxt = "Down";
    }
    return resultTxt;
}


