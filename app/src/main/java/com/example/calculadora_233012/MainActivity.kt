package com.example.calculadora_233012

import android.hardware.biometrics.BiometricManager.Strings
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora_233012.databinding.ActivityMainBinding
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var canAddOperation = false
    private var canAddDecimal = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun numberAction(view: View)
    {
        if(view is Button)
        {
            if(view.text == "")
                binding.workingsTV.append(view.text)
            binding.workingsTV.append(view.text)
            canAddOperation = true
        }
    }

    fun operationAction(view: View)
    {
        if (view is Button && canAddOperation) {
            val buttonText = view.text.toString()
            if (buttonText == "^") {
                binding.workingsTV.append("^")
                canAddOperation = false
                canAddDecimal = true
            } else {
                binding.workingsTV.append(buttonText)
                canAddOperation = false
                canAddDecimal = true
            }
        }
    }

    fun allClearAction(view: View){
        binding.workingsTV.text=""
        binding.resultsTV.text=""
    }

    fun backspaceAction(view: View){
        val length = binding.workingsTV.length()
        if(length > 0)
            binding.workingsTV.text = binding.workingsTV.text.subSequence(0, length -1)
    }

    fun equalsAction(view : View)
    {
        binding.resultsTV.text = calculateResutls()
    }

    private fun calculateResutls() : String
    {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return ""

        val result = addSubstractCalculate(timesDivision)
        return result.toString()

    }

    private fun addSubstractCalculate(passedList: MutableList<Any>): Float
    {
        var result = passedList[0] as Float

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex)
            {
                val operator = passedList[i]
                val nextDigit = passedList[i+1] as Float
                if(operator == '+')
                    result += nextDigit
                if(operator == '-')
                    result -= nextDigit
            }
        }
        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any>
    {
        var list = passedList
        while (list.contains('x') || list.contains('/') || list.contains('^')) {
            list = when {
                list.contains('^') -> calculateExponential(list)
                else -> calcTimesDiv(list)
            }
        }
        return list
    }

    private fun calculateExponential(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val base = passedList[i - 1] as Float
                val exponent = passedList[i + 1] as Float
                if (operator == '^') {
                    newList.add(Math.pow(base.toDouble(), exponent.toDouble()).toFloat())
                } else {
                    newList.add(base)
                    newList.add(operator)
                }
            }

            if (i == passedList.lastIndex) {
                newList.add(passedList[i])
            }
        }
        return newList
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any>
    {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex && i<restartIndex)
            {
                val operator = passedList[i]
                val prevDigit = passedList[i-1] as Float
                val nextDigit = passedList[i+1] as Float
                when(operator)
                {
                    'x' ->
                    {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/'->
                    {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else->
                    {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if(i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any>
    {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(character in binding.workingsTV.text)
        {
            if(character.isDigit() || character == '.')
                currentDigit  += character
            else
            {
                list.add(currentDigit.toFloat())
                currentDigit=""
                list.add(character)
            }
        }

        if(currentDigit != "")
            list.add(currentDigit.toFloat())

        return list
    }
}