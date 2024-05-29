# Получение директории, где лежит скрипт
$directory = Split-Path -Parent $MyInvocation.MyCommand.Path

# Получение всех .ogg файлов в директории
$files = Get-ChildItem -Path $directory -Filter *.ogg

foreach ($file in $files) {
    # Извлечение имени файла без расширения
    $baseName = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
    
    # Разделение имени на элементы: первая буква и остальные символы
    if ($baseName -match '^([a-zA-Z])(\d+)$') {
        $firstLetter = $matches[1]
        $digits = $matches[2]
        
        # Формирование нового имени в зависимости от количества цифр
        if ($digits.Length -eq 2) {
            $newName = "note_{0}_sharp_{1}" -f $firstLetter.ToUpper(), $digits.Substring($digits.Length - 1)
        } elseif ($digits.Length -eq 1) {
            $newName = "note_{0}_{1}" -f $firstLetter.ToUpper(), $digits
        } else {
            # Пропустить файлы, которые не соответствуют условиям
            continue
        }
        
        # Полный путь нового имени
        $newFilePath = Join-Path -Path $directory -ChildPath "$newName.ogg"
        
        # Переименование файла
        Rename-Item -Path $file.FullName -NewName $newFilePath
    }
}

Write-Output "Переименование файлов завершено."
