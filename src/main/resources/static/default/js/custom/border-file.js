
    // DATA TABLES
    // =================================================================
    // Require Data Tables
    // -----------------------------------------------------------------
    // http://www.datatables.net/
    // =================================================================

    attachFile = {
        idx: 0,
        add: function () { // 파일필드 추가
            var o = this;
            var idx = o.idx;

            var div = document.createElement('div');
            div.className="form-inline"
            div.style.marginTop = '3px';
            div.id = 'file' + o.idx;

            var file = document.all ? document.createElement('<input name="files">') : document.createElement('input');
            file.type = 'file';
            file.name = 'files';
            file.size = '40';
            file.multiple = 'multiple'
            file.id = 'fileField' + o.idx;

            var btn = document.createElement('input');
            btn.type = 'button';
            btn.value = '삭제';
            btn.onclick = function () {
                o.del(idx)
            }
            btn.style.marginLeft = '5px';

            div.appendChild(file);
            div.appendChild(btn);
            document.getElementById('attachFileDiv').appendChild(div);

            o.idx++;
        },
        del: function (idx) { // 파일필드 삭제
            if (document.getElementById('fileField' + idx).value != '' && !confirm('삭제 하시겠습니까?')) {
                return;
            }
            document.getElementById('attachFileDiv').removeChild(document.getElementById('file' + idx));
        }
    }

    $(document).ready( function() {

        $("input[type=file]").change(function () {
            var fileInput = document.getElementById("contract_file");
            var files = fileInput.files;
            var file;

            $("#file_list").html("");
            for (var i = 0; i < files.length; i++) {
                file = files[i];
                $("#file_list").append(file.name + "<br>");
            }
        });
    });
