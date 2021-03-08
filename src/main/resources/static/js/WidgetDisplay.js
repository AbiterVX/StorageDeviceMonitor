
//【左侧导航栏】设置左侧导航栏当前选中的导航项
function setCurrentNavItem(navItemIndex){
    var navItem = document.getElementById("NavItem" + navItemIndex);
    navItem.style.color ="black";
    navItem.style.fontWeight = "bold";
}