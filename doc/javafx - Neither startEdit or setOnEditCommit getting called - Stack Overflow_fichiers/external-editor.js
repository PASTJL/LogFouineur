"use strict";StackExchange.mockups=function(){function t(t,e,n,i,o){function a(t,e,n){for(var i=-1,o=-1;;){if(o=e.indexOf(t,o+1),-1==o)break;(0>i||Math.abs(o-n)<Math.abs(o-i))&&(i=o)}return i}return t.replace(new RegExp("<!-- Begin mockup[^>]*? -->\\s*!\\[[^\\]]*\\]\\((http://[^ )]+)[^)]*\\)\\s*<!-- End mockup -->","g"),function(t,r,s){var c={"payload":r.replace(/[^-A-Za-z0-9+&@#\/%?=~_|!:,.;\(\)]/g,""),"pos":a(t,e,s),"len":t.length};return-1===c.pos?t:(o.push(c),t+"\n\n"+n+i+"-"+(o.length-1)+"%")})}function e(){StackExchange.externalEditor.init({"thingName":"mockup","thingFinder":t,"getIframeUrl":function(t){var e="/plugins/mockups/editor";return t&&(e+="?edit="+encodeURIComponent(t)),e},"buttonTooltip":"UI wireframe","buttonImageUrl":"/content/Shared/Balsamiq/wmd-mockup-button.png","onShow":function(t){window.addMockupToEditor=t},"onRemove":function(){window.addMockupToEditor=null;try{delete window.addMockupToEditor}catch(t){}}})}return{"init":e}}(),StackExchange.schematics=function(){function t(){if(!window.postMessage)return i;var t=document.createElement("div");t.innerHTML="<svg/>";var e="http://www.w3.org/2000/svg"==(t.firstChild&&t.firstChild.namespaceURI);if(!e)return i;var n=navigator.userAgent;return/Firefox|Chrome/.test(n)?r:/Apple/.test(navigator.vendor)||/Opera/.test(n)?a:o}function e(t,e,n,i,o){function a(t,e,n){for(var i=-1,o=-1;;){if(o=e.indexOf(t,o+1),-1==o)break;(0>i||Math.abs(o-n)<Math.abs(o-i))&&(i=o)}return i}return t.replace(new RegExp("<!-- Begin schematic[^>]*? -->\\s*!\\[[^\\]]*\\]\\((http://[^ )]+)[^)]*\\)\\s*<!-- End schematic -->","g"),function(t,r,s){var c={"payload":r.replace(/[^-A-Za-z0-9+&@#\/%?=~_|!:,.;\(\)]/g,""),"pos":a(t,e,s),"len":t.length};return-1===c.pos?t:(o.push(c),t+"\n\n"+n+i+"-"+(o.length-1)+"%")})}function n(){var n;StackExchange.externalEditor.init({"thingName":"schematic","thingFinder":e,"getIframeUrl":function(t){var e="/plugins/schematics/editor";return t&&(e+="?edit="+encodeURIComponent(t)),e},"buttonTooltip":"Schematic","buttonImageUrl":"/content/Sites/electronics/img/wmd-schematic-button.png?v=1","checkSupport":function(){var e=t();switch(e){case r:return!0;case a:return confirm("Your browser is not officially supported by the schematics editor; however it has been reported to work. Launch the editor?");case o:return confirm("Your browser is not officially supported by the schematics editor; it may or may not work. Launch the editor anyway?");case i:return alert("Sorry, your browser does not support all the necessary features for the schematics editor."),!1}},"onShow":function(t){var e=$("<div class='popup' />").css("z-index",1111).text("Loading editor").appendTo("body").show().addSpinner({"marginLeft":5}).center({"dy":-200});$("<div style='text-align:right;margin-top: 10px' />").append($("<button>cancel</button>").click(function(){e.remove(),t()})).appendTo(e),n=function(n){if(n=n.originalEvent,"https://www.circuitlab.com"===n.origin){n.data||t();var i=$.parseJSON(n.data);if(i&&"success"===i.load)return e.remove(),void 0;if(i&&i.edit_url&&i.image_url){i.fkey=StackExchange.options.user.fkey;var o=$("<div class='popup' />").css("z-index",1111).appendTo("body").show(),a=function(){o.text("Storing image").addSpinner({"marginLeft":5}).center(),$.post("/plugins/schematics/save",i).done(function(e){o.remove(),t(e.img)}).fail(function(t){if(409===t.status){var e="Storing aborted";t.responseText.length<200&&(e=t.responseText),o.text(e+", will retry shortly").addSpinner({"marginLeft":5}).center(),setTimeout(a,1e4)}else o.remove(),alert("Failed to upload the schematic image.")})};a()}}},$(window).on("message",n)},"onRemove":function(){$(window).off("message",n)}})}var i=0,o=1,a=2,r=3;return{"init":n}}(),StackExchange.externalEditor=function(){function t(t){function e(t,e){function f(e){function i(){StackExchange.helpers.closePopups(m.add(o)),u()}var o,s=v||b.caret(),c=b[0].value||"",h=e?e.pos:s.start,d=e?e.len:s.end-s.start,f=c.substring(0,h),p=c.substring(h+d);v=null;var g=function(e,o){if(!e)return setTimeout(i,0),b.focus(),void 0;StackExchange.navPrevention.start();var a=void 0===o?n(e):o,r=f.replace(/(?:\r\n|\r|\n){1,2}$/,""),c=r+a+p.replace(/^(?:\r\n|\r|\n){1,2}/,""),l=s.start+a.length-f.length+r.length;setTimeout(function(){t.textOperation(function(){b.val(c).focus().caret(l,l)}),i()},0)},m=null;if(a){var y=a(e?e.payload:null);m=$("<iframe>",{"src":y})}else{var w=r(e?e.payload:null);m=$(w)}m.addClass("esc-remove").css({"position":"fixed","top":"2.5%","left":"2.5%","width":"95%","height":"95%","background":"white","z-index":1001}),$("body").loadPopup({"html":m,"target":$("body"),"lightbox":!0}).done(function(){$(window).resize(),l(g)})}$('<style type="text/css"> .wmd-'+i+"-button span { background-position: 0 0; } .wmd-"+i+"-button:hover span { background-position: 0 -40px; }</style>)").appendTo("head");var p,g,v,m=t.getConverter().hooks,b=$("#wmd-input"+e);b.on("keyup",function(t){var e=t.keyCode||t.charCode;if(8===e||46===e){var n=b.caret().start;b.caret(n,n)}}),m.chain("preConversion",function(t){var e=(t.match(/%/g)||[]).length,n=b.length?b[0].value||"":"";return p=new Array(e+2).join("%"),g=[],o(t,n,p,i,g)}),m.chain("postConversion",function(t){return t.replace(new RegExp(p+i+"-(\\d+)%","g"),function(t,e){return"<sup><a href='#' class='edit-"+i+"' data-id='"+e+"'>"+h+"</a></sup>"})});var y="The "+i+" editor does not support touch devices.",w=!1;$("#wmd-preview"+e).on("touchend",function(){w=!0}).on("click","a.edit-"+i,function(){return w?(alert(y),w=!1,!1):(w=!1,(!d||d())&&f(g[$(this).attr("data-id")]),!1)}),$("#wmd-input"+e).keyup(function(t){t.shiftKey||t.altKey||t.metaKey||!t.ctrlKey||77!==t.which||(!d||d())&&f()}),setTimeout(function(){var t=($("#wmd-button-bar"+e),$("#wmd-image-button"+e)),n=$("<li class='wmd-button wmd-"+i+"-button' id='wmd-"+i+"-button"+e+"' title='"+s+" Ctrl-M' />").insertAfter(t),o=!1,a=$("<span />").css({"backgroundImage":"url("+c+")"}).appendTo(n).on("touchend",function(){o=!0}).click(function(){return o?(alert(y),o=!1,void 0):(o=!1,(!d||d())&&f(),void 0)});$.browser.msie&&a.mousedown(function(){v=b.caret()})},0)}function n(t){return('\n\n<!-- Begin {THING}: In order to preserve an editable {THING}, please\n     don\'t edit this section directly.\n     Click the "edit" link below the image in the preview instead. -->\n\n![{THING}]('+t+")\n\n<!-- End {THING} -->\n\n").replace(/{THING}/g,i)}var i=t.thingName,o=t.thingFinder,a=t.getIframeUrl,r=t.getDivContent,s=t.buttonTooltip,c=t.buttonImageUrl,l=t.onShow,u=t.onRemove||function(){},h=t.editLabel||"edit the above "+i,d=t.checkSupport;StackExchange.MarkdownEditor.creationCallbacks.add(e)}return{"init":t}}();